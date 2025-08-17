export interface APIResponseDTO<T> {
  status: string;
  data: T;
  message: string;
}

export interface AuthenticationResponseDTO {
  access_token: string;
  refresh_token: string;
}
