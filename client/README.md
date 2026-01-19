# React Client

[![codecov](https://codecov.io/github/JakubPavlicek/is-stag/graph/badge.svg?token=G9QCAP4G9J)](https://codecov.io/github/JakubPavlicek/is-stag)
[![Frontend CI](https://github.com/JakubPavlicek/is-stag/actions/workflows/frontend-ci.yaml/badge.svg)](https://github.com/JakubPavlicek/is-stag/actions/workflows/frontend-ci.yaml)
[![React](https://img.shields.io/badge/React-20232A?logo=react&logoColor=61DAFB)](https://react.dev/)
[![TypeScript](https://img.shields.io/badge/TypeScript-007ACC?logo=typescript&logoColor=white)](https://www.typescriptlang.org/)
[![Vite](https://img.shields.io/badge/Vite-646CFF?logo=vite&logoColor=white)](https://vitejs.dev/)

The modern web interface for the IS/STAG University Portal. Built with performance and developer experience in mind,
leveraging the latest React ecosystem tools.

---

## ⚡️ Tech Stack

* **Core:** [React 19](https://react.dev/), [TypeScript](https://www.typescriptlang.org/)
* **Build Tool:** [Vite](https://vitejs.dev/)
* **Styling:** [Tailwind CSS](https://tailwindcss.com/), [Shadcn UI](https://ui.shadcn.com/)
* **Routing:** [TanStack Router](https://tanstack.com/router)
* **State/Data Fetching:** [TanStack Query](https://tanstack.com/query/latest)
* **Auth:** Keycloak Integration (OIDC)

---

## 🚀 Getting Started

### Prerequisites

* **Node.js**: v25.3.0 or higher
* **npm**: v11.7.0 or higher

### Installation

1. **Clone & Install**
   ```bash
   cd client
   npm install
   ```

### Development

Start the development server with Hot Module Replacement (HMR):

```bash
npm run dev
```

The application will be available at [http://localhost:5173](http://localhost:5173).

### Code Generation

We use OpenAPI to generate typed API clients from the backend specifications. If the backend API changes:

```bash
npm run gen:api
```

---

## 🏗 Project Structure

```text
src/
├── api/             # Generated API clients (do not edit manually)
├── components/      # Reusable UI building blocks
│   ├── features/    # Domain-specific components (e.g., StudentCard)
│   ├── layout/      # App shell (Sidebar, Header, Footer)
│   ├── theme/       # Theming components (Dark mode toggle, Color schemes)
│   └── ui/          # Low-level primitives (Shadcn UI: Button, Input)
├── hooks/           # Custom React hooks
├── lib/             # Utilities, Auth hooks, Axios config
├── routes/          # TanStack Router definitions
├── test/            # Testing setup
└── main.tsx         # App entry point
```

---

## Preview

### Home Screen (Logged In)
![Home Screen Logged In](../images/home_screen_logged_in.png)

### Home Screen (Dark Mode / Logged Off)
![Home Screen Dark](../images/home_screen_logged_off_dark.png)

### My Data Overview
![My Data Screen 1](../images/my_data_screen_1.png)
![My Data Screen 2](../images/my_data_screen_2.png)

### Personal Info Form
![Personal Info Form](../images/personal_info_form.png)
