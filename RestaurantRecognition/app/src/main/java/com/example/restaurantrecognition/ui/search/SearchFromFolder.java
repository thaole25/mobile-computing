package com.example.restaurantrecognition.ui.search;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.restaurantrecognition.R;
import com.example.restaurantrecognition.ml_model.AnalyseImageOnFirebase;
import com.example.restaurantrecognition.ui.help.HelpViewModel;

import java.io.IOException;

import butterknife.BindString;
import butterknife.BindView;

import static android.app.Activity.RESULT_OK;

public class SearchFromFolder extends Fragment {
//    @BindView(R.id.imageContainer)
//    ImageView imageContainer;
//    @BindView(R.id.txtResult)
//    TextView txtResult;

    private AnalyseImageOnFirebase aiModel = new AnalyseImageOnFirebase();
    private final int REQUEST_CODE_GET_IMAGE = 25;
    private SearchFromFolderModel searchFromFolderModel;
    private TextView txt_prediction;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        searchFromFolderModel = ViewModelProviders.of(this).get(SearchFromFolderModel.class);
        View root = inflater.inflate(R.layout.fragment_search_from_folder, container, false);
        txt_prediction = root.findViewById(R.id.text_prediction);

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT); //ACTION_OPEN_DOCUMENT
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE_GET_IMAGE);

//        View root = inflater.inflate(R.layout.fragment_search_from_folder, container, false);
//        final TextView textView = root.findViewById(R.id.text_recent_matches);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GET_IMAGE) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                Bitmap imageBitmap = null;
                try {
                    imageBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), imageUri);
                    CharSequence prediction = aiModel.sendImagetoFirebase(imageBitmap);
                    searchFromFolderModel.getText().observe(this, new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            txt_prediction.setText(prediction);

                        }
                    });

//                    txtResult.setText(prediction);
//                    imageContainer.setImageBitmap(imageBitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
