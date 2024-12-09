import { AxiosError } from 'axios';
import router from '../../router';

const errorPageRedirects: Record<number, string> = {
  401: '/unauthorized',
  403: '/forbidden',
  408: '/request-timeout',
  500: '/internal-server-error',
  503: '/service-unavailable',
};

export default function AxiosErrorResponseHandler(
  error: AxiosError,
  statusCode: number
): void {
  const redirectPath = errorPageRedirects[statusCode];

  if (redirectPath) {
    console.error(`Redirecting to ${redirectPath} due to error:`, error);
    router.navigate(redirectPath);
  } else {
    console.error('Unhandled error:', error, 'Status code:', statusCode);
  }
}