package wgu.jbas127.frontiercompanion.ui.models;

import java.util.List;

import wgu.jbas127.frontiercompanion.data.entities.Article;
import wgu.jbas127.frontiercompanion.data.entities.Exhibit;
import wgu.jbas127.frontiercompanion.data.entities.ExhibitPanel;

public abstract class DisplayableItem {

    public static class TitleItem extends DisplayableItem {
        public final Exhibit exhibit;

        public TitleItem(Exhibit exhibit) {
            this.exhibit = exhibit;
        }
    }

    public static class PanelItem extends DisplayableItem {
        public final ExhibitPanel panel;

        public PanelItem(ExhibitPanel panel) {
            this.panel = panel;
        }
    }

    public static class ActionItem extends DisplayableItem {
        public final List<Article> articles;
        public final Exhibit exhibit;

        public ActionItem(List<Article> articles, Exhibit exhibit) {
            this.articles = articles;
            this.exhibit = exhibit;
        }
    }
}
