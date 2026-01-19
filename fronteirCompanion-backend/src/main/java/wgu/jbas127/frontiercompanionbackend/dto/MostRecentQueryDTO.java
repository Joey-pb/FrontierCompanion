package wgu.jbas127.frontiercompanionbackend.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class MostRecentQueryDTO{
    private final String queryText;
    private final int resultCount;
    private final Long clickedArticleId;
    private final LocalDateTime searchTimestamp;

}
