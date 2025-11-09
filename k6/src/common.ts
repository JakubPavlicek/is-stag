import faker from 'k6/x/faker';

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

/**
 * Generates a payload for the PATCH /persons/{personId} endpoint.
 * It randomly includes fields to simulate partial updates.
 */
export function generateUpdatePersonPayload(): UpdatePersonRequest {
  const payload: UpdatePersonRequest = {};

  if (Math.random() < 0.7) {
    payload.contact = {
      email: faker.person.email(),
      mobile: faker.person.phone(),
    };
  }

  if (Math.random() < 0.4) {
    payload.temporaryAddress = {
      street: faker.address.streetName(),
      streetNumber: faker.address.streetNumber(),
      municipality: faker.address.city(),
      zipCode: faker.address.zip(),
      country: faker.address.country(),
    };
  }

  // Ensure the payload is not empty
  if (Object.keys(payload).length === 0) {
    payload.contact = { email: faker.person.email() };
  }

  return payload;
}
