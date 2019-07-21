/*
 * Copyright (C) 2016 AriaLyy(https://github.com/AriaLyy/Aria)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arialyy.simple.core.download.group;

import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import com.arialyy.annotations.DownloadGroup;
import com.arialyy.aria.core.Aria;
import com.arialyy.aria.core.common.controller.ControllerType;
import com.arialyy.aria.core.download.DownloadGroupEntity;
import com.arialyy.aria.core.download.DownloadGroupTask;
import com.arialyy.frame.util.show.L;
import com.arialyy.frame.util.show.T;
import com.arialyy.simple.R;
import com.arialyy.simple.base.BaseActivity;
import com.arialyy.simple.databinding.ActivityDownloadGroupBinding;
import com.arialyy.simple.util.AppUtil;
import com.arialyy.simple.widget.SubStateLinearLayout;

/**
 * Created by lyy on 2017/7/6.
 */
public class FTPDirDownloadActivity extends BaseActivity<ActivityDownloadGroupBinding> {
  private static final String dir = "ftp://9.9.9.50:21/upload/测试";

  private SubStateLinearLayout mChildList;
  private DownloadGroupEntity mEntity;

  @Override protected void init(Bundle savedInstanceState) {
    super.init(savedInstanceState);
    Aria.download(this).register();
    setTitle("FTP文件夹下载");
    mChildList = findViewById(R.id.child_list);
    mEntity = Aria.download(this).getFtpDirEntity(dir);
    if (mEntity != null) {
      mChildList.addData(mEntity.getSubEntities());
      getBinding().setFileSize(mEntity.getConvertFileSize());
      if (mEntity.getFileSize() == 0) {
        getBinding().setProgress(0);
      } else {
        getBinding().setProgress(mEntity.isComplete() ? 100
            : (int) (mEntity.getCurrentProgress() * 100 / mEntity.getFileSize()));
      }
    }
  }

  @Override protected int setLayoutId() {
    return R.layout.activity_download_group;
  }

  public void onClick(View view) {
    switch (view.getId()) {
      case R.id.start:
        if (!AppUtil.chekEntityValid(mEntity)) {
          Aria.download(this)
              .loadFtpDir(dir)
              .setDirPath(
                  Environment.getExternalStorageDirectory().getPath() + "/Download/ftp_dir")
              .setGroupAlias("ftp文件夹下载")
              .option()
              .login("lao", "123456")
              .controller(ControllerType.START_CONTROLLER)
              .start();
          break;
        }
        if (Aria.download(this).loadFtpDir(mEntity.getId()).isRunning()) {
          Aria.download(this).loadFtpDir(mEntity.getId()).stop();
        } else {
          Aria.download(this).loadFtpDir(mEntity.getId()).resume();
        }
        break;
      case R.id.cancel:
        if (AppUtil.chekEntityValid(mEntity)) {
          Aria.download(this).loadFtpDir(mEntity.getId()).cancel();
        }
        break;
    }
  }

  @DownloadGroup.onPre() protected void onPre(DownloadGroupTask task) {
    L.d(TAG, "group pre");
  }

  @DownloadGroup.onTaskPre() protected void onTaskPre(DownloadGroupTask task) {
    if (mChildList.getSubData().size() <= 0) {
      mChildList.addData(task.getEntity().getSubEntities());
    }
    L.d(TAG, "group task pre");
    getBinding().setFileSize(task.getConvertFileSize());
  }

  @DownloadGroup.onTaskStart() void taskStart(DownloadGroupTask task) {
    L.d(TAG, "group task start");
  }

  @DownloadGroup.onTaskRunning() protected void running(DownloadGroupTask task) {
    getBinding().setProgress(task.getPercent());
    getBinding().setSpeed(task.getConvertSpeed());
    mChildList.updateChildProgress(task.getEntity().getSubEntities());
  }

  @DownloadGroup.onTaskResume() void taskResume(DownloadGroupTask task) {
    L.d(TAG, "group task resume");
  }

  @DownloadGroup.onTaskStop() void taskStop(DownloadGroupTask task) {
    L.d(TAG, "group task stop");
    getBinding().setSpeed("");
  }

  @DownloadGroup.onTaskCancel() void taskCancel(DownloadGroupTask task) {
    getBinding().setSpeed("");
    getBinding().setProgress(0);
  }

  @DownloadGroup.onTaskFail() void taskFail(DownloadGroupTask task) {
    L.d(TAG, "group task fail");
  }

  @DownloadGroup.onTaskComplete() void taskComplete(DownloadGroupTask task) {
    getBinding().setProgress(100);
    mChildList.updateChildProgress(task.getEntity().getSubEntities());
    T.showShort(this, "任务组下载完成");
    L.d(TAG, "任务组下载完成");
  }
}
