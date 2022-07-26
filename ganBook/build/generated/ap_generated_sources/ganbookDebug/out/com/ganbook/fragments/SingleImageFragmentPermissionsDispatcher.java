// This file was generated by PermissionsDispatcher. Do not modify!
package com.ganbook.fragments;

import java.lang.String;
import permissions.dispatcher.PermissionUtils;

final class SingleImageFragmentPermissionsDispatcher {
  private static final int REQUEST_SHARECURRENTPIC = 9;

  private static final String[] PERMISSION_SHARECURRENTPIC = new String[] {"android.permission.WRITE_EXTERNAL_STORAGE"};

  private static final int REQUEST_SAVECURRENTIMAGE = 10;

  private static final String[] PERMISSION_SAVECURRENTIMAGE = new String[] {"android.permission.WRITE_EXTERNAL_STORAGE"};

  private SingleImageFragmentPermissionsDispatcher() {
  }

  static void shareCurrentPicWithCheck(SingleImageFragment target) {
    if (PermissionUtils.hasSelfPermissions(target.getActivity(), PERMISSION_SHARECURRENTPIC)) {
      target.shareCurrentPic();
    } else {
      target.requestPermissions(PERMISSION_SHARECURRENTPIC, REQUEST_SHARECURRENTPIC);
    }
  }

  static void saveCurrentImageWithCheck(SingleImageFragment target) {
    if (PermissionUtils.hasSelfPermissions(target.getActivity(), PERMISSION_SAVECURRENTIMAGE)) {
      target.saveCurrentImage();
    } else {
      target.requestPermissions(PERMISSION_SAVECURRENTIMAGE, REQUEST_SAVECURRENTIMAGE);
    }
  }

  static void onRequestPermissionsResult(SingleImageFragment target, int requestCode,
      int[] grantResults) {
    switch (requestCode) {
      case REQUEST_SHARECURRENTPIC:
      if (PermissionUtils.getTargetSdkVersion(target.getActivity()) < 23 && !PermissionUtils.hasSelfPermissions(target.getActivity(), PERMISSION_SHARECURRENTPIC)) {
        return;
      }
      if (PermissionUtils.verifyPermissions(grantResults)) {
        target.shareCurrentPic();
      }
      break;
      case REQUEST_SAVECURRENTIMAGE:
      if (PermissionUtils.getTargetSdkVersion(target.getActivity()) < 23 && !PermissionUtils.hasSelfPermissions(target.getActivity(), PERMISSION_SAVECURRENTIMAGE)) {
        return;
      }
      if (PermissionUtils.verifyPermissions(grantResults)) {
        target.saveCurrentImage();
      }
      break;
      default:
      break;
    }
  }
}