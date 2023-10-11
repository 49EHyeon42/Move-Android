package dev.ehyeon.moveapplication.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import dagger.hilt.android.AndroidEntryPoint;
import dev.ehyeon.moveapplication.databinding.RecordFragmentBinding;

@AndroidEntryPoint
public class RecordFragment extends Fragment {

    private RecordFragmentBinding binding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = RecordFragmentBinding.inflate(inflater, container, false);

        binding.recordFragmentSwipeRefreshLayout.setOnRefreshListener(() -> {
            Toast.makeText(requireContext(), "Test Toast Message", Toast.LENGTH_SHORT).show();

            binding.recordFragmentSwipeRefreshLayout.setRefreshing(false);
        });

        return binding.getRoot();
    }
}
