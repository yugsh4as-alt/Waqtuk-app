package com.prayertimes.app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnErrorListener;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.github.barteksc.pdfviewer.util.FitPolicy;
import com.prayertimes.app.R;
import com.prayertimes.app.utils.AppPreferences;
import com.shockwave.pdfium.PdfiumCore;

public class QuranFragment extends Fragment {

    private static final String ASSET_NAME = "quran_warsh.pdf";

    private PDFView       pdfView;
    private ProgressBar   progressPdf;
    private TextView      tvPageNumber;
    private TextView      tvError;
    private AppPreferences prefs;

    private int currentPage = 0;

    @Override
    public void onCreate(@Nullable Bundle b) {
        super.onCreate(b);
        prefs = new AppPreferences(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inf, @Nullable ViewGroup c, @Nullable Bundle b) {
        return inf.inflate(R.layout.fragment_quran, c, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle b) {
        pdfView      = v.findViewById(R.id.pdf_view);
        progressPdf  = v.findViewById(R.id.progress_pdf);
        tvPageNumber = v.findViewById(R.id.tv_page_number);
        tvError      = v.findViewById(R.id.tv_pdf_error);

        int savedPage = prefs.getLastQuranPage();

        v.findViewById(R.id.btn_bookmark).setOnClickListener(x -> {
            prefs.setQuranBookmark(currentPage);
            Toast.makeText(requireContext(),
                "تم حفظ الصفحة " + (currentPage + 1), Toast.LENGTH_SHORT).show();
        });

        loadPdf(savedPage);
    }

    private void loadPdf(int startPage) {
        progressPdf.setVisibility(View.VISIBLE);
        tvError.setVisibility(View.GONE);

        try {
            pdfView.fromAsset(ASSET_NAME)
                .defaultPage(startPage)
                .onLoad(new OnLoadCompleteListener() {
                    @Override
                    public void loadComplete(int nbPages) {
                        if (!isAdded()) return;
                        progressPdf.setVisibility(View.GONE);
                        updatePageLabel(startPage, nbPages);
                    }
                })
                .onPageChange(new OnPageChangeListener() {
                    @Override
                    public void onPageChanged(int page, int pageCount) {
                        currentPage = page;
                        prefs.setLastQuranPage(page);
                        updatePageLabel(page, pageCount);
                    }
                })
                .onError(new OnErrorListener() {
                    @Override
                    public void onError(Throwable t) {
                        if (!isAdded()) return;
                        progressPdf.setVisibility(View.GONE);
                        tvError.setVisibility(View.VISIBLE);
                    }
                })
                .scrollHandle(new DefaultScrollHandle(requireContext()))
                .spacing(4)
                .pageFitPolicy(FitPolicy.WIDTH)
                .pageSnap(true)
                .pageFling(true)
                .nightMode(false)
                .enableAnnotationRendering(false)
                .enableAntialiasing(true)
                .load();
        } catch (Exception e) {
            progressPdf.setVisibility(View.GONE);
            tvError.setVisibility(View.VISIBLE);
        }
    }

    private void updatePageLabel(int page, int total) {
        if (tvPageNumber != null) {
            tvPageNumber.setText("صفحة " + (page + 1) + " / " + total);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (pdfView != null) {
            pdfView.recycle();
        }
    }
}
