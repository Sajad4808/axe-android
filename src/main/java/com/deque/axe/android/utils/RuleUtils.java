package com.deque.axe.android.utils;

import com.deque.axe.android.AxeDevice;
import com.deque.axe.android.AxeView;

import java.util.LinkedList;
import java.util.List;

public class RuleUtils {
  private static LinkedListEx<AxeView> views;
  private static int screenHeight;
  private static int screenWidth;

  //todo: check if the Elevation property pushes rendering down the hierarchy
  static class LinkedListEx<T> extends LinkedList<AxeView> {
    public AxeView get(AxeView axeView) {
      AxeView current;
      for (AxeView view : this) {
        current = view;
        if (current.axeViewId.equals(axeView.axeViewId)) {
          return current;
        }
      }
      return null;
    }

    int getIndex(AxeView axeView) {
      AxeView current;
      for(int i = 0; i < this.size(); i++ ) {
        current = this.get(i);
        if (current.axeViewId.equals(axeView.axeViewId)) {
          return i;
        }
      }
      return 0;
    }
  }

  public static void setViews(AxeView rootView, AxeDevice axeDevice) {
    screenHeight = axeDevice.screenHeight;
    screenWidth = axeDevice.screenWidth;
    views = new LinkedListEx<>();

    rootView.children.forEach(axeView -> {
      if (!axeView.isOffScreen(axeView.boundsInScreen, screenHeight,screenWidth)) {
        if (!axeView.className.endsWith("ContentFrameLayout")) {
          views.add(axeView);
        }
        if(axeView.children.size() > 0) {
          recurse(axeView);
        }
      }
    });
  }

  private static void recurse(AxeView axeView) {
    axeView.children.forEach(child -> {
      if (!child.isOffScreen(child.boundsInScreen, screenHeight, screenWidth)) {
        if (!child.className.endsWith("ContentFrameLayout")) {
          views.add(child);
        }
        if (child.children.size() > 0) {
          recurse(child);
        }
      }
    });
  }

  public static boolean isObscured(AxeView axeView) {
    int index = views.getIndex(axeView);
    List<AxeView> split = views.subList(index, views.size());

    for (AxeView view : split) {
      if (!view.axeViewId.equals(axeView.axeViewId)
              && view.overlaps(axeView.boundsInScreen)){
        return true;
      }
    }
    return false;
  }
}
