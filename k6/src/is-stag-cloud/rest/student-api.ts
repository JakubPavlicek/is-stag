import { getRequest } from './client.ts';

export function getStudentProfile(studentId: string) {
  return getRequest({
    path: `/students/${encodeURIComponent(studentId)}`,
    tagName: 'GET /students/{studentId}',
  });
}
