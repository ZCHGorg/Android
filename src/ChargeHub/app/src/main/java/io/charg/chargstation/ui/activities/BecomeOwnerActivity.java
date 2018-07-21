package io.charg.chargstation.ui.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.github.barteksc.pdfviewer.PDFView;

import butterknife.BindView;
import io.charg.chargstation.R;

public class BecomeOwnerActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.pdf_view)
    PDFView mPdfView;

    @Override
    public int getResourceId() {
        return R.layout.activity_become_owner;
    }

    @Override
    public void onActivate() {
        initToolbar();
        loadPdf();
    }

    private void loadPdf() {
        mPdfView.fromAsset("plan.pdf")
                .enableSwipe(true)
                .enableDoubletap(true)
                .load();
    }

    private void initToolbar() {
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
