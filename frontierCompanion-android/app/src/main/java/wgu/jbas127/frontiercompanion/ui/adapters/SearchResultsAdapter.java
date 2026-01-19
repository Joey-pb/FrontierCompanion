package wgu.jbas127.frontiercompanion.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import wgu.jbas127.frontiercompanion.data.models.NarrativeDTO;
import wgu.jbas127.frontiercompanion.databinding.ItemSearchResultBinding;

public class SearchResultsAdapter extends ListAdapter<NarrativeDTO, SearchResultsAdapter.SearchResultViewHolder> {

    public SearchResultsAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public SearchResultsAdapter.SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemSearchResultBinding binding = ItemSearchResultBinding.inflate(inflater, parent, false);

        return new SearchResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultsAdapter.SearchResultViewHolder holder, int position) {
        NarrativeDTO currentNarrative = getItem(position);
        holder.bind(currentNarrative);
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder {

        private final ItemSearchResultBinding binding;


        public SearchResultViewHolder(ItemSearchResultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(NarrativeDTO narrative) {
            binding.setNarrative(narrative);
            binding.executePendingBindings();

            binding.headerLayout.setOnClickListener(v -> {
                boolean isExpanded = binding.contentText.getVisibility() == View.VISIBLE;
                binding.contentText.setVisibility(isExpanded ? View.GONE : View.VISIBLE);
                binding.expandArrowImage.animate().rotation(isExpanded ? 0f : 180f).setDuration(300).start();
            });
        }
    }

    private static final DiffUtil.ItemCallback<NarrativeDTO> DIFF_CALLBACK = new DiffUtil.ItemCallback<NarrativeDTO>() {
        @Override
        public boolean areItemsTheSame(@NonNull NarrativeDTO oldItem, @NonNull NarrativeDTO newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull NarrativeDTO oldItem, @NonNull NarrativeDTO newItem) {
            return oldItem.equals(newItem);
        }
    };
}


