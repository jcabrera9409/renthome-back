export interface APIResponseDTO<T> {
  status: string;
  data: T;
  message: string;
}

export interface AuthenticationResponseDTO {
  accessToken: string;
  refreshToken: string;
}
