package com.github.zhixinghey0712.bilibiliplayer.ui;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.zhixinghey0712.bilibiliplayer.R;
import com.github.zhixinghey0712.bilibiliplayer.util.GlobalVariables;
import com.github.zhixinghey0712.bilibiliplayer.util.UpdateMode;
import com.github.zhixinghey0712.bilibiliplayer.util.info.LocalInfoManager;
import com.github.zhixinghey0712.bilibiliplayer.util.info.Network;
import com.github.zhixinghey0712.bilibiliplayer.util.json.favlist.FavListJsonBean;
import com.github.zhixinghey0712.bilibiliplayer.util.json.user.UserInfoJsonBean;

import org.w3c.dom.Text;

import java.util.Objects;

public class UserFragment extends Fragment {
    private View view;
    private Handler uiHandler;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_user, container, false);
        Button loginButton = view.findViewById(R.id.user_commit_info_button);

        uiHandler = new Handler();
        loginButton.setOnClickListener(v -> {
            updateUI(UpdateMode.ONLINE);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(GlobalVariables.TAG, "Refresh UI");
        updateUI(UpdateMode.LOCAL);
    }

    private void updateUI(UpdateMode mode) {
        if (mode == UpdateMode.ONLINE) {
            // 在线更新
            TextView uidBox = view.findViewById(R.id.user_uid_input);
            String uid = uidBox.getText().toString();
            if (!Objects.equals(uid, ""))
                new updateUserInfo(uid, view, uiHandler).start();
        } else if (mode == UpdateMode.LOCAL) {
            // 本地更新
            // 如果本地文件有缺失就停止
            if (!LocalInfoManager.isFileExists(GlobalVariables.USER_FACE_FILE_NAME)) return;
            if (!LocalInfoManager.isFileExists(GlobalVariables.USER_INFO_FILE_NAME)) return;
            new updateUserInfo(view, uiHandler).start();
        }
    }

    static class updateUserInfo extends Thread {
        private String Uid;
        private View view;
        private Handler ui;
        private Bitmap face;
        private UserInfoJsonBean info;
        private String name;
        private UpdateMode mode;

        /**
         * 在线更新
         *
         * @param uid  uid
         * @param view view
         * @param ui   Handler
         */
        public updateUserInfo(String uid, View view, Handler ui) {
            Uid = uid;
            this.view = view;
            this.ui = ui;
            this.mode = UpdateMode.ONLINE;
        }

        public updateUserInfo(View view, Handler ui) {
            Uid = "-1";
            this.view = view;
            this.ui = ui;
            this.mode = UpdateMode.LOCAL;
        }

        private void updateUserInfoUI() {
            ui.post(() -> {
                LinearLayout uidLayout = view.findViewById(R.id.user_uid_layout);
                TextView uidText = view.findViewById(R.id.user_uid_textbox);
                TextView nameText = view.findViewById(R.id.user_name_box);
                ImageView faceView = view.findViewById(R.id.user_face_image);
                uidLayout.setVisibility(View.VISIBLE);
                faceView.setImageBitmap(face);
                nameText.setText((CharSequence) name);
                uidText.setText((CharSequence) Uid);
            });
        }

        @Override
        public void run() {
            info = LocalInfoManager.getUserInfo(Uid, mode);
            Uid = String.valueOf(info.getData().getMid());
            name = info.getData().getName();
            face = LocalInfoManager.saveUserFace(info.getData().getFace(), mode);
            updateUserInfoUI();
        }
    }
}