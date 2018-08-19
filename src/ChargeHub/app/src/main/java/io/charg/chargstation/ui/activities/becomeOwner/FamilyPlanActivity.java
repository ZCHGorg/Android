package io.charg.chargstation.ui.activities.becomeOwner;

import com.github.barteksc.pdfviewer.PDFView;

import butterknife.BindView;
import io.charg.chargstation.R;
import io.charg.chargstation.ui.activities.BaseActivity;

public class FamilyPlanActivity extends BaseActivity {

    @BindView(R.id.pdf_view)
    PDFView mPdfView;

    @Override
    public int getResourceId() {
        return R.layout.activity_family_plan;
    }

    @Override
    public void onActivate() {
        loadPdf();
    }

    private void loadPdf() {
        mPdfView.fromAsset("plan.pdf")
                .enableSwipe(true)
                .enableDoubletap(true)
                .load();
    }
}
