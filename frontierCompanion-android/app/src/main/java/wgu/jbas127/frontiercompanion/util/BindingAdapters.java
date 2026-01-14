package wgu.jbas127.frontiercompanion.util;

import android.content.Context;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.BindingAdapter;

public class BindingAdapters {
    @BindingAdapter("imageUrl")
    public static void setImageUrl(ImageView imageView, String resourceName) {
        if (resourceName == null || resourceName.isEmpty()) {
            return;
        }

        Context context = imageView.getContext();
        int resourceId = context.getResources().getIdentifier(
                resourceName,
                "drawable",
                context.getPackageName()
        );

        if (resourceId != 0) {
            imageView.setImageResource(resourceId);
        }
    }

    @BindingAdapter("contentPosition")
    public static void setContentPosition(TextView textView, String position) {
        if (position == null || !(textView.getParent() instanceof ConstraintLayout)) {
            return;
        }

        ConstraintLayout parent = (ConstraintLayout) textView.getParent();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(parent);

        constraintSet.clear(textView.getId(), ConstraintSet.TOP);
        constraintSet.clear(textView.getId(), ConstraintSet.BOTTOM);
        constraintSet.clear(textView.getId(), ConstraintSet.START);
        constraintSet.clear(textView.getId(), ConstraintSet.END);

        int horizontalMarginInDp = 24;
        int verticalMarginInDp = 90;

        int horizontalMarginInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                horizontalMarginInDp,
                textView.getResources().getDisplayMetrics()
        );
        int verticalMarginInPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                verticalMarginInDp,
                textView.getResources().getDisplayMetrics()
        );

        constraintSet.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START, horizontalMarginInPx);
        constraintSet.connect(textView.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);



        switch (position) {
            case "TOP":
                constraintSet.connect(textView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP, verticalMarginInPx);
                break;
            case "CENTER":
                constraintSet.connect(textView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
                constraintSet.connect(textView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
                break;
            case "BOTTOM":
            default:
                constraintSet.connect(textView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM, verticalMarginInPx);
                break;
        }

        constraintSet.applyTo(parent);
    }
}
