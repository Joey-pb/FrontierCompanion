package wgu.jbas127.frontiercompanion.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import wgu.jbas127.frontiercompanion.R;
import wgu.jbas127.frontiercompanion.data.entities.Article;
import wgu.jbas127.frontiercompanion.data.entities.ExhibitPanel;
import wgu.jbas127.frontiercompanion.databinding.ItemActionPanelBinding;
import wgu.jbas127.frontiercompanion.databinding.ItemExhibitPanelBinding;
import wgu.jbas127.frontiercompanion.databinding.ItemTitlePanelBinding;
import wgu.jbas127.frontiercompanion.ui.models.DisplayableItem;

public class PanelAdapter extends ListAdapter<DisplayableItem, RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_TITLE = 1;
    private static final int VIEW_TYPE_PANEL = 2;
    private static final int VIEW_TYPE_ACTION = 3;

    private final OnActionPanelClickListener actionClickListener;

    public interface OnActionPanelClickListener {
        void onShowOnMapClicked(long exhibitId);
        void onCreateRouteClicked(long exhibitId);
        void onBackToTopClicked();
        void onArticleClicked(Article article);
    }

    public PanelAdapter(OnActionPanelClickListener actionClickListener) {
        super(DIFF_CALLBACK);
        this.actionClickListener = actionClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        DisplayableItem item = getItem(position);
        if (item instanceof DisplayableItem.TitleItem) {
            return VIEW_TYPE_TITLE;
        } else if (item instanceof DisplayableItem.PanelItem) {
            return VIEW_TYPE_PANEL;
        } else {
            return VIEW_TYPE_ACTION;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_TITLE:
                ItemTitlePanelBinding titleBinding = DataBindingUtil.inflate(
                        inflater,
                        R.layout.item_title_panel,
                        parent,
                        false
                );
                return new TitleViewHolder(titleBinding, actionClickListener);
            case VIEW_TYPE_ACTION:
                ItemActionPanelBinding actionBinding = DataBindingUtil.inflate(
                        inflater,
                        R.layout.item_action_panel,
                        parent,
                        false
                        );
                return new ActionViewHolder(actionBinding, actionClickListener);
            case VIEW_TYPE_PANEL:
            default:
                ItemExhibitPanelBinding panelBinding = DataBindingUtil.inflate(
                        inflater,
                        R.layout.item_exhibit_panel,
                        parent,
                        false
                );
                return new PanelViewHolder(panelBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DisplayableItem item = getItem(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_TITLE:
                ((TitleViewHolder) holder).bind((DisplayableItem.TitleItem) item);
                break;
            case VIEW_TYPE_PANEL:
                ((PanelViewHolder) holder).bind((DisplayableItem.PanelItem) item);
                break;
            case VIEW_TYPE_ACTION:
                ((ActionViewHolder) holder).bind((DisplayableItem.ActionItem) item);
                break;
        }
    }

    // -- ViewHolder for the TITLE slide --
    class TitleViewHolder extends RecyclerView.ViewHolder {
        private final ItemTitlePanelBinding binding;

        public TitleViewHolder(ItemTitlePanelBinding binding, OnActionPanelClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;

            binding.buttonLocateMap.setOnClickListener(v -> {
                if (binding.getExhibit() != null) {
                    listener.onShowOnMapClicked(binding.getExhibit().getId());
                }
            });

            binding.buttonCreateRoute.setOnClickListener(v -> {
                if (binding.getExhibit() != null) {
                    listener.onCreateRouteClicked(binding.getExhibit().getId());
                }
            });
        }

        void bind(DisplayableItem.TitleItem item) {
            binding.setExhibit(item.exhibit);
            binding.executePendingBindings();
        }
    }

    // -- ViewHolder for PANEL slides --
    class PanelViewHolder extends RecyclerView.ViewHolder {
        private final ItemExhibitPanelBinding binding;

        public PanelViewHolder(ItemExhibitPanelBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(DisplayableItem.PanelItem item) {
           binding.setPanel(item.panel);
           binding.executePendingBindings();
        }
    }

    // -- ViewHolder for ACTION slides --
    class ActionViewHolder extends RecyclerView.ViewHolder {
        private final ItemActionPanelBinding binding;
        private final OnActionPanelClickListener listener;


        public ActionViewHolder(ItemActionPanelBinding binding, OnActionPanelClickListener listener) {
            super(binding.getRoot());
            this.binding = binding;
            this.listener = listener;
        }

        void bind(DisplayableItem.ActionItem item) {
            binding.setExhibit(item.exhibit);

            StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
            binding.recyclerViewArticles.setLayoutManager(layoutManager);

            ArticleGridAdapter articleAdapter = new ArticleGridAdapter(article -> {
                if (listener != null) {
                    listener.onArticleClicked(article);
                }
            });

            binding.recyclerViewArticles.setAdapter(articleAdapter);
            articleAdapter.submitList(item.articles);

            binding.buttonLocateMapAction.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onShowOnMapClicked(item.exhibit.getId());
                }
            });
            binding.buttonCreateRouteAction.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onCreateRouteClicked(item.exhibit.getId());
                }
            });
            binding.buttonBackToTop.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onBackToTopClicked();
                }
            });

            binding.executePendingBindings();
        }
    }

    private static final DiffUtil.ItemCallback<DisplayableItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<DisplayableItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull DisplayableItem oldItem, @NonNull DisplayableItem newItem) {
            if (oldItem instanceof DisplayableItem.TitleItem && newItem instanceof DisplayableItem.TitleItem) {
                return ((DisplayableItem.TitleItem) oldItem).exhibit.getId() == ((DisplayableItem.TitleItem) newItem).exhibit.getId();
            }
            if (oldItem instanceof DisplayableItem.PanelItem && newItem instanceof DisplayableItem.PanelItem) {
                return ((DisplayableItem.PanelItem) oldItem).panel.getId() == ((DisplayableItem.PanelItem) newItem).panel.getId();
            }
            if (oldItem instanceof DisplayableItem.ActionItem && newItem instanceof DisplayableItem.ActionItem) {
                return true;
            }
            return false;
        }

        @Override
        public boolean areContentsTheSame(@NonNull DisplayableItem oldItem, @NonNull DisplayableItem newItem) {
            if (oldItem instanceof DisplayableItem.TitleItem && newItem instanceof DisplayableItem.TitleItem) {
                return ((DisplayableItem.TitleItem) oldItem).exhibit.equals(((DisplayableItem.TitleItem) newItem).exhibit);
            }
            if (oldItem instanceof DisplayableItem.PanelItem && newItem instanceof DisplayableItem.PanelItem) {
                return ((DisplayableItem.PanelItem) oldItem).panel.equals(((DisplayableItem.PanelItem) newItem).panel);
            }
            if (oldItem instanceof DisplayableItem.ActionItem && newItem instanceof DisplayableItem.ActionItem) {
                // Compare the list of articles to see if they've changed.
                return ((DisplayableItem.ActionItem) oldItem).articles.equals(((DisplayableItem.ActionItem) newItem).articles);
            }
            return false;
        }
    };
}
