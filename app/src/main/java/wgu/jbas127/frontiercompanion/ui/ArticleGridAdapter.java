package wgu.jbas127.frontiercompanion.ui;

import android.text.Layout;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import wgu.jbas127.frontiercompanion.R;
import wgu.jbas127.frontiercompanion.data.entities.Article;
import wgu.jbas127.frontiercompanion.databinding.ItemArticleGridBinding;

public class ArticleGridAdapter extends ListAdapter<Article, ArticleGridAdapter.ArticleViewHolder> {
    public interface OnArticleClickListener {
        void onArticleClicked(Article article);
    }

    private final OnArticleClickListener clickListener;

    public ArticleGridAdapter(OnArticleClickListener clickListener) {
        super(DIFF_CALLBACK);;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemArticleGridBinding binding = DataBindingUtil.inflate(
                inflater,
                R.layout.item_article_grid,
                parent,
                false);

        return new ArticleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        Article currentArticle = getItem(position);
        holder.bind(currentArticle, clickListener);
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        private final ItemArticleGridBinding binding;

        public ArticleViewHolder(ItemArticleGridBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(final Article article, final OnArticleClickListener listener) {
            binding.setArticle(article);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onArticleClicked(article);
                }
            });

            binding.executePendingBindings();
        }
    }

    private static final DiffUtil.ItemCallback<Article> DIFF_CALLBACK = new DiffUtil.ItemCallback<Article>() {
        @Override
        public boolean areItemsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Article oldItem, @NonNull Article newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getThumbnailPath().equals(newItem.getThumbnailPath());
        }
    };
}
