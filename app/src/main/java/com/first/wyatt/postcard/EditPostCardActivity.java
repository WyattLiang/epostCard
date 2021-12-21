package com.first.wyatt.postcard;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EditPostCardActivity extends AppCompatActivity {

    private static final int TARGET_WIDTH = 380;
    private static final int TARGET_HEIGHT =241;
    private String currentPhotoPath;
    private ImageButton add_photo;
    private ImageView image_view;
    private EditText content;
    private EditText toWho;
    private EditText fromWho;
    private Button preview;
    private Uri uri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post_card);

        image_view = findViewById(R.id.select_photo);
        add_photo = findViewById(R.id.add_photo);
        preview = findViewById(R.id.edit_info);
        content = findViewById(R.id.content_postcard);
        toWho = findViewById(R.id.to_id);
        fromWho = findViewById(R.id.by_id);
    }

    public void showPopUpMenu(View v){
        PopupMenu popup = new PopupMenu(EditPostCardActivity.this, v);
        try {
            Field[] fields = popup.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popup);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        popup.getMenuInflater().inflate(R.menu.menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.open_camera){
                    takePhotoIntent();
                }else if(id == R.id.open_gallery){
                    ChooseProfileImage();
                }

                Toast.makeText(getApplicationContext(), "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        popup.show();
    }

    ActivityResultLauncher<Intent> mCamIntentResult = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>(){
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == Activity.RESULT_OK){
                        File f = new File(currentPhotoPath);
                        uri = Uri.fromFile(f);
                        Picasso.with(EditPostCardActivity.this).load(uri).resize(TARGET_WIDTH,TARGET_HEIGHT).centerCrop().into(image_view);
                        add_photo.setVisibility(View.GONE);
                    }
                }
            });

    private void takePhotoIntent(){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {

            // Create the File where the photo should go
            File photoFile = null;
            try{
                photoFile = createImageFile();
                // Continue only if the File was successfully created
                if(photoFile != null){
                    Uri uri = FileProvider.getUriForFile(EditPostCardActivity.this,"com.example.epostcard",photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,uri);
                    takePictureIntent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    mCamIntentResult.launch(takePictureIntent);
                    //startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                }

            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    ActivityResultLauncher<String> mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(),
            new ActivityResultCallback<Uri>(){
                @Override
                public void onActivityResult(Uri result) {
                    uri = result;
                    Picasso.with(EditPostCardActivity.this).load(uri).resize(TARGET_WIDTH,TARGET_HEIGHT).centerCrop().into(image_view);
                    Log.d("URI FROM CHOSEN",uri+"");
                    add_photo.setVisibility(View.GONE);
                }
            });

    private void ChooseProfileImage(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mGetContent.launch("image/*");
            }
        });
    }

    private File createImageFile() throws IOException {
        String fileName = String.valueOf(System.currentTimeMillis());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        File image = File.createTempFile(
                fileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    public Bitmap getBitmapFromView(View view)
    {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    /*preview postcard after user has done edit*/
    public void previewPostCard(View view) throws IOException {
        if(!String.valueOf(uri).isEmpty() && !TextUtils.isEmpty(content.getText().toString())){
            RelativeLayout relativeview = findViewById(R.id.postcard_view); /*make a whole layout an image, including all views in this layout*/
            Bitmap bm = getBitmapFromView(relativeview);
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); /*create a file path to save this postcard image*/
            String fileName = String.valueOf(System.currentTimeMillis());
            File postcard = File.createTempFile(
                    fileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
            OutputStream stream = new FileOutputStream(postcard);
            bm.compress(Bitmap.CompressFormat.JPEG,100,stream); /*make image*/
            stream.flush();
            stream.close();

            uri = Uri.parse(postcard.getAbsolutePath());  /*get the postcard uri*/

            Intent intent = new Intent(EditPostCardActivity.this, PreviewPostCardActivity.class);
            intent.putExtra("Image",uri.toString());
            startActivity(intent);
        }
        else{
            Toast.makeText(EditPostCardActivity.this,"Please choose your image and fill out content above",Toast.LENGTH_LONG).show();
        }
    }

}