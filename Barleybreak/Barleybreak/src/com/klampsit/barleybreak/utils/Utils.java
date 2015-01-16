package com.klampsit.barleybreak.utils;

import com.klampsit.barleybreak.game.field.Coord;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

public class Utils {
    public static final String SIZE_DELIMITER = "-";
    public static final int CROP_ACTION = 100;

    public static String getRealPathFromURI(ContentResolver cr, Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = cr.query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public static Dir another(Dir dir) {
        switch (dir) {
        case LEFT:
            return Dir.RIGHT;
        case RIGHT:
            return Dir.LEFT;
        case UP:
            return Dir.DOWN;
        case DOWN:
            return Dir.UP;
        default:
            return null;
        }
    }

    public static Bitmap scaleBitmap(Bitmap bmp, float tsw, float tsh) {
        return Bitmap.createScaledBitmap(bmp, (int) tsw, (int) tsh, true);
    }

    public static Bitmap[] divideToParts(Bitmap image, int xparts, int yparts) {
        Bitmap[] res = new Bitmap[yparts * xparts];
        final int pixelByCol = image.getWidth() / xparts;
        final int pixelByRow = image.getHeight() / yparts;
        int k = 1;

        for (int j = 0; j < yparts; j++) {
            for (int i = 0; i < xparts; i++) {
                int startX = pixelByCol * i;
                int startY = pixelByRow * j;
                res[k == xparts * yparts ? 0 : k++] = Bitmap.createBitmap(image,
                    startX,
                    startY,
                    pixelByCol,
                    pixelByRow);
            }
        }

        return res;
    }

    public static void hideTitleAndActionBar(Activity aAct) {
        aAct.requestWindowFeature(Window.FEATURE_NO_TITLE);
        aAct.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
        View decorView = aAct.getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_LOW_PROFILE;
        decorView.setSystemUiVisibility(uiOptions);
    }

    public static Drawable getDrawableFromPath(Resources aRes, String aPath) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inDither = false;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inTempStorage = new byte[32 * 1024];
        opts.inSampleSize = 4;
        return new BitmapDrawable(aRes, BitmapFactory.decodeFile(aPath, opts));
    }

    public static Bitmap rotate(Bitmap aBmp, float aDegree) {
        Matrix matrix = new Matrix();
        matrix.postRotate(aDegree);

        return Bitmap.createBitmap(aBmp, 0, 0, aBmp.getWidth(), aBmp.getHeight(), matrix, true);
    }

    public static void fadeIn(View aV, long aMs) {
        AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
        fadeIn.setDuration(aMs);
        fadeIn.setFillAfter(true);
        aV.startAnimation(fadeIn);
    }

    public static int getOrientation(Activity aActivity) {
        return aActivity.getResources().getConfiguration().orientation;
    }

    public static void setOrientation(Activity aActivity, boolean aOrient) {
        aActivity.setRequestedOrientation(aOrient ? 0 : 1);
    }

    public static Coord<Integer> parseCoord(String aCoords) {
        String[] parts = aCoords.split(SIZE_DELIMITER);
        return new Coord<Integer>(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }

    public static String coordToString(Coord<Integer> aCoords) {
        return String.format("%d%s%d", SIZE_DELIMITER, aCoords.x, aCoords.y);
    }

    public static void performCrop(Activity src, Uri picUri, Uri outputUri) {
        try {
            int aspectX = 2000;
            int aspectY = 1200;

            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(picUri, "image/*");
            intent.putExtra("scale", "true");
            intent.putExtra("aspectX", aspectX);
            intent.putExtra("aspectY", aspectY);
            intent.putExtra("scaleUpIfNeeded", true);
            intent.putExtra("return-data", false);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);

            src.startActivityForResult(intent, CROP_ACTION);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Your device doesn't support the crop action!";
            Toast toast = Toast.makeText(src, errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
