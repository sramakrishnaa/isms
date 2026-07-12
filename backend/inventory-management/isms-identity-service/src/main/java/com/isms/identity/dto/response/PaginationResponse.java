package com.isms.identity.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaginationResponse<T> {
	List<T> list;
	long totalCount;
}
