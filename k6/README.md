# k6 Load Tests

[![k6 CI](https://github.com/JakubPavlicek/is-stag/actions/workflows/k6-ci.yaml/badge.svg)](https://github.com/JakubPavlicek/is-stag/actions/workflows/k6-ci.yaml)
[![TypeScript](https://img.shields.io/badge/TypeScript-007ACC?logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![k6](https://img.shields.io/badge/k6-646CFF?logo=k6&logoColor=white)](https://k6.io/)

This directory contains k6 load tests for two targets:

- `src/is-stag-cloud/` – load tests for this project's REST API implementation
- `src/is-stag-ws/` – load tests for the original IS/STAG WebServices

## Prerequisites

1. Install the package dependencies:

   ```shell
   npm install
   ```

2. Make sure the `k6` binary is installed and available in your shell `PATH`.
3. Make sure Prometheus in the Kubernetes cluster is running and reachable before you start a test with remote write output. These scripts use `-o experimental-prometheus-rw`, so the run will fail to export metrics if the cluster Prometheus endpoint is not available.

## Available scripts

### Load-test runs

- `npm run test:is-stag-cloud` – runs the load test against this project's REST API
- `npm run test:is-stag-ws` – runs the load test against the original IS/STAG WebServices

### Local quality checks

- `npm run lint` – runs ESLint
- `npm run lint:fix` – runs ESLint with automatic fixes
- `npm run format` – formats files with Prettier
- `npm run format:check` – verifies Prettier formatting
- `npm run typecheck` – runs TypeScript type checking

## Common remote write environment variables

The example commands below use Prometheus remote write output.

| Name                                        | Description                                            | Default value                                                     |
|---------------------------------------------|--------------------------------------------------------|-------------------------------------------------------------------|
| `K6_PROMETHEUS_RW_SERVER_URL`               | Prometheus remote write endpoint.                      | None, must be provided when using remote write.                   |
| `K6_PROMETHEUS_RW_TREND_STATS`              | Exported trend aggregations sent to Prometheus.        | None, must be provided explicitly if you want custom trend stats. |
| `K6_PROMETHEUS_RW_INSECURE_SKIP_TLS_VERIFY` | Disables TLS verification for the remote write target. | `false`                                                           |

## IS/STAG WebServices Load Tests

This suite targets the original IS/STAG WebServices in `src/is-stag-ws/`.

| Name             | Description                                                      | Default value                                                      |
|------------------|------------------------------------------------------------------|--------------------------------------------------------------------|
| `K6_TEST_RUN_ID` | Custom label for distinguishing test runs in Prometheus/Grafana. | `1`                                                                |
| `BASE_URL`       | IS/STAG WebServices base URL.                                    | `https://stag-demo.zcu.cz/ws/services/rest2`                       |
| `WSCOOKIE`       | Authentication cookie for IS/STAG WebServices.                   | `8240b2482c79e3c578a61d45912bd360315d8ed7875a4a9e19eaa52ba688f141` |

Example:

```shell
npm run test:is-stag-ws -- \
  -e K6_PROMETHEUS_RW_SERVER_URL=https://prometheus.is-stag.internal/api/v1/write \
  -e K6_PROMETHEUS_RW_TREND_STATS='p(90),p(95),p(99),min,max,avg,med,count,sum' \
  -e K6_PROMETHEUS_RW_INSECURE_SKIP_TLS_VERIFY=true \
  -e K6_TEST_RUN_ID=1 \
  -e WSCOOKIE=8240b2482c79e3c578a61d45912bd360315d8ed7875a4a9e19eaa52ba688f141
```

## IS/STAG Cloud Load Tests

This suite targets this project's REST API in `src/is-stag-cloud/`.

| Name             | Description                                                      | Default value               |
|------------------|------------------------------------------------------------------|-----------------------------|
| `K6_TEST_RUN_ID` | Custom label for distinguishing test runs in Prometheus/Grafana. | `1`                         |
| `BASE_URL`       | Base URL of this project's REST API.                             | `https://is-stag.cz/api/v1` |

Example:

```shell
npm run test:is-stag-cloud -- \
  -e K6_PROMETHEUS_RW_SERVER_URL=https://prometheus.is-stag.internal/api/v1/write \
  -e K6_PROMETHEUS_RW_TREND_STATS='p(90),p(95),p(99),min,max,avg,med,count,sum' \
  -e K6_PROMETHEUS_RW_INSECURE_SKIP_TLS_VERIFY=true \
  -e K6_TEST_RUN_ID=2
```
