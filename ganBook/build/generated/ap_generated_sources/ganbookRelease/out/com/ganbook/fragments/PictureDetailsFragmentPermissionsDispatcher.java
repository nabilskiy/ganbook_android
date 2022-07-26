// This file was generated by PermissionsDispatcher. Do not modify!
package com.ganbook.fragments;

import com.ganbook.models.PictureAnswer;
import java.lang.Override;
import java.lang.String;
import java.lang.ref.WeakReference;
import permissions.dispatcher.GrantableRequest;
import permissions.dispatcher.PermissionUtils;

final class PictureDetailsFragmentPermissionsDispatcher {
  private static final int REQUEST_SHAREPIC = 7;

  private static final String[] PERMISSION_SHAREPIC = new String[] {"android.permission.WRITE_EXTERNAL_STORAGE"};

  private static GrantableRequest PENDING_SHAREPIC;

  private static final int REQUEST_SAVEPIC = 8;

  private static final String[] PERMISSION_SAVEPIC = new String[] {"android.permission.WRITE_EXTERNAL_STORAGE"};

  private static GrantableRequest PENDING_SAVEPIC;

  private PictureDetailsFragmentPermissionsDispatcher() {
  }

  static void sharePicWithCheck(PictureDetailsFragment target, PictureAnswer pictureAnswer,
      String ganId, String classId) {
    if (PermissionUtils.hasSelfPermissions(target.getActivity(), PERMISSION_SHAREPIC)) {
      target.sharePic(pictureAnswer, ganId, classId);
    } else {
      PENDING_SHAREPIC = new SharePicPermissionRequest(target, pictureAnswer, ganId, classId);
      target.requestPermissions(PERMISSION_SHAREPIC, REQUEST_SHAREPIC);
    }
  }

  static void savePicWithCheck(PictureDetailsFragment target, PictureAnswer pictureAnswer,
      String ganId, String classId) {
    if (PermissionUtils.hasSelfPermissions(target.getActivity(), PERMISSION_SAVEPIC)) {
      target.savePic(pictureAnswer, ganId, classId);
    } else {
      PENDING_SAVEPIC = new SavePicPermissionRequest(target, pictureAnswer, ganId, classId);
      target.requestPermissions(PERMISSION_SAVEPIC, REQUEST_SAVEPIC);
    }
  }

  static void onRequestPermissionsResult(PictureDetailsFragment target, int requestCode,
      int[] grantResults) {
    switch (requestCode) {
      case REQUEST_SHAREPIC:
      if (PermissionUtils.getTargetSdkVersion(target.getActivity()) < 23 && !PermissionUtils.hasSelfPermissions(target.getActivity(), PERMISSION_SHAREPIC)) {
        return;
      }
      if (PermissionUtils.verifyPermissions(grantResults)) {
        if (PENDING_SHAREPIC != null) {
          PENDING_SHAREPIC.grant();
        }
      }
      PENDING_SHAREPIC = null;
      break;
      case REQUEST_SAVEPIC:
      if (PermissionUtils.getTargetSdkVersion(target.getActivity()) < 23 && !PermissionUtils.hasSelfPermissions(target.getActivity(), PERMISSION_SAVEPIC)) {
        return;
      }
      if (PermissionUtils.verifyPermissions(grantResults)) {
        if (PENDING_SAVEPIC != null) {
          PENDING_SAVEPIC.grant();
        }
      }
      PENDING_SAVEPIC = null;
      break;
      default:
      break;
    }
  }

  private static final class SharePicPermissionRequest implements GrantableRequest {
    private final WeakReference<PictureDetailsFragment> weakTarget;

    private final PictureAnswer pictureAnswer;

    private final String ganId;

    private final String classId;

    private SharePicPermissionRequest(PictureDetailsFragment target, PictureAnswer pictureAnswer,
        String ganId, String classId) {
      this.weakTarget = new WeakReference<PictureDetailsFragment>(target);
      this.pictureAnswer = pictureAnswer;
      this.ganId = ganId;
      this.classId = classId;
    }

    @Override
    public void proceed() {
      PictureDetailsFragment target = weakTarget.get();
      if (target == null) return;
      target.requestPermissions(PERMISSION_SHAREPIC, REQUEST_SHAREPIC);
    }

    @Override
    public void cancel() {
    }

    @Override
    public void grant() {
      PictureDetailsFragment target = weakTarget.get();
      if (target == null) return;
      target.sharePic(pictureAnswer, ganId, classId);
    }
  }

  private static final class SavePicPermissionRequest implements GrantableRequest {
    private final WeakReference<PictureDetailsFragment> weakTarget;

    private final PictureAnswer pictureAnswer;

    private final String ganId;

    private final String classId;

    private SavePicPermissionRequest(PictureDetailsFragment target, PictureAnswer pictureAnswer,
        String ganId, String classId) {
      this.weakTarget = new WeakReference<PictureDetailsFragment>(target);
      this.pictureAnswer = pictureAnswer;
      this.ganId = ganId;
      this.classId = classId;
    }

    @Override
    public void proceed() {
      PictureDetailsFragment target = weakTarget.get();
      if (target == null) return;
      target.requestPermissions(PERMISSION_SAVEPIC, REQUEST_SAVEPIC);
    }

    @Override
    public void cancel() {
    }

    @Override
    public void grant() {
      PictureDetailsFragment target = weakTarget.get();
      if (target == null) return;
      target.savePic(pictureAnswer, ganId, classId);
    }
  }
}