import grpc from 'k6/net/grpc';
import { check } from 'k6';
import { GRPC_URL, TEST_RUN_ID } from '../shared/common.ts';

const client = new grpc.Client();

export function connect() {
  client.connect(GRPC_URL, { plaintext: true, reflect: true });
}

export function close() {
  client.close();
}

export function getCodelistValues(request: any) {
  const res = client.invoke('stag.platform.codelist.v1.CodelistService/GetCodelistValues', request, {
    tags: { testId: TEST_RUN_ID },
  });

  check(res, {
    'GetCodelistValues: status is OK': (r) => r.status === grpc.StatusOK,
  });

  return res;
}

export function getPersonProfileData(request: any) {
  const res = client.invoke('stag.platform.codelist.v1.CodelistService/GetPersonProfileData', request, {
    tags: { testId: TEST_RUN_ID },
  });

  check(res, {
    'GetPersonProfileData: status is OK': (r) => r.status === grpc.StatusOK,
  });

  return res;
}

export function getPersonAddressData(request: any) {
  const res = client.invoke(
    'stag.platform.codelist.v1.CodelistService/GetPersonAddressData',
    request,
    {
      tags: { testId: TEST_RUN_ID },
    },
  );
  check(res, {
    'GetPersonAddressData: status is OK': (r) => r.status === grpc.StatusOK,
  });
  return res;
}

export function getPersonBankingData(request: any) {
  const res = client.invoke(
    'stag.platform.codelist.v1.CodelistService/GetPersonBankingData',
    request,
    {
      tags: { testId: TEST_RUN_ID },
    },
  );
  check(res, {
    'GetPersonBankingData: status is OK': (r) => r.status === grpc.StatusOK,
  });
  return res;
}

export function getPersonEducationData(request: any) {
  const res = client.invoke(
    'stag.platform.codelist.v1.CodelistService/GetPersonEducationData',
    request,
    {
      tags: { testId: TEST_RUN_ID },
    },
  );
  check(res, {
    'GetPersonEducationData: status is OK': (r) => r.status === grpc.StatusOK,
  });
  return res;
}
