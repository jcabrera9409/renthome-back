export interface APIResponseDTO<T> {
  success: boolean;
  message: string;
  data: T;
  statusCode: number;
  timestamp: string;
}

export interface AuthenticationResponseDTO {
  access_token: string;
  refresh_token: string;
}

export interface ChangeStatusRequest {
    id: number;
    estado: boolean;
    estadoString: String;
}

export interface PageableResponseDTO<T> {
  content: T[];
  links: any[];
  page: {
      number: number;
      size: number;
      totalElements: number;
      totalPages: number;
  }
}

export interface CasaDTO {
  id: number;
  nombre: string;
  direccion: string;
}