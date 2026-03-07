import { getRequest, patchRequest } from './client.ts';

interface ContactUpdate {
  email?: string;
  phone?: string;
  mobile?: string;
  dataBox?: string;
}

interface AddressUpdate {
  country?: string;
  district?: string;
  municipality?: string;
  municipalityPart?: string;
  street?: string;
  streetNumber?: string;
  zipCode?: string;
  postOffice?: string;
}

export interface UpdatePersonRequest {
  contact?: ContactUpdate;
  temporaryAddress?: AddressUpdate;
}

export function getPersonProfile(personId: number) {
  return getRequest({
    path: `/persons/${personId}`,
    tagName: 'GET /persons/{personId}',
  });
}

export function getPersonAddresses(personId: number) {
  return getRequest({
    path: `/persons/${personId}/addresses`,
    tagName: 'GET /persons/{personId}/addresses',
  });
}

export function getPersonBanking(personId: number) {
  return getRequest({
    path: `/persons/${personId}/banking`,
    tagName: 'GET /persons/{personId}/banking',
  });
}

export function getPersonEducation(personId: number) {
  return getRequest({
    path: `/persons/${personId}/education`,
    tagName: 'GET /persons/{personId}/education',
  });
}

export function updatePersonProfile(personId: number, payload: UpdatePersonRequest) {
  return patchRequest({
    path: `/persons/${personId}`,
    body: payload,
    tagName: 'PATCH /persons/{personId}',
    expectedStatus: 204,
  });
}
