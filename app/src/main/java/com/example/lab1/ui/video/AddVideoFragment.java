package com.example.lab1.ui.video;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.lab1.R;
import com.example.lab1.data.DBHelper;
import com.example.lab1.databinding.FragmentAddVideoBinding;


public class AddVideoFragment extends Fragment {

    private FragmentAddVideoBinding binding;
    private DBHelper db;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddVideoBinding.inflate(inflater, container, false);
        db = new DBHelper(requireContext());

        binding.btnSave.setOnClickListener(v -> save());

        return binding.getRoot();
    }

    private void save() {
        String title = binding.etTitle.getText().toString();
        String desc = binding.etDesc.getText().toString();
        String url = binding.etUrl.getText().toString();
        String thumb = binding.etThumb.getText().toString();
//        String subs = binding.etSubs.getText().toString();

        VideoMaterial v = new VideoMaterial(title, desc, url, thumb);
//        v.setSubtitlesUrl(subs);

        db.insertVideo(v);
        Toast.makeText(getContext(), "Video added", Toast.LENGTH_SHORT).show();
        NavHostFragment.findNavController(this).popBackStack();
    }
}