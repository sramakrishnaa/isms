export interface UserSearchRequest {
  pageIndex: number;
  pageSize: number;
  searchText?: string;
  sortBy?: string;
  sortDirection?: string;
}