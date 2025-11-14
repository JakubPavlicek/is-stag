import faker from 'k6/x/faker';
import { SharedArray } from 'k6/data';

// The test run ID
export const TEST_RUN_ID = __ENV.K6_TEST_RUN_ID || '1';

// The base URL of the REST API
export const BASE_URL = __ENV.BASE_URL || 'https://is-stag.cz';
// The gRPC server address
export const GRPC_URL = __ENV.GRPC_URL || 'localhost:9090';
// The path to the data directory
export const DATA_DIR = '../../data';
// The path to the proto files
export const PROTO_DIR = '../../../proto';

export const HEADERS = {
  'Accept-Language': 'cs',
  'Content-Type': 'application/json',
};

// The range of person IDs.
// IDs are chosen randomly from this range to include some "not found" cases.
export const MIN_PERSON_ID = 100;
export const MAX_PERSON_ID = 235000;

// A list of known student IDs, loaded from a JSON file.
export const STUDENT_IDS = new SharedArray(
  'student-ids',
  function (): string[] {
    return JSON.parse(open(`${DATA_DIR}/student-ids.json`));
  },
);

// A list of known codelist domains.
export const CODELIST_DOMAINS = new SharedArray(
  'codelist-domains',
  function (): string[] {
    return JSON.parse(open(`${DATA_DIR}/codelist-domains.json`));
  },
);

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
