package io.charg.chargstation.ui.activities.becomeOwner;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.github.barteksc.pdfviewer.PDFView;

import butterknife.BindView;
import io.charg.chargstation.R;
import io.charg.chargstation.ui.activities.BaseActivity;

public class FamilyPlanActivity extends BaseActivity {

    @BindView(R.id.pdf_view)
    PDFView mPdfView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    public int getResourceId() {
        return R.layout.activity_family_plan;
    }

    @Override
    public void onActivate() {
        initToolbar();
        loadPdf();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }


    private void loadPdf() {
        mPdfView.fromAsset("plan.pdf")
                .enableSwipe(true)
                .enableDoubletap(true)
                .load();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
