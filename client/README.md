# React Client

This is the frontend application for the University Portal (IS/STAG).

## Prerequisites

- Node.js (v25.2.1+)
- npm (v11.6.3+)

## Setup

1. Install dependencies:

   ```bash
   npm install
   ```

2. Generate API clients (if backend specs changed):
   ```bash
   npm run gen:api
   ```

## Development

Start the development server:

```bash
npm run dev
```

The application will be available at `http://localhost:5173`.

## Building

Build the application for production:

```bash
npm run build
```

## Project Structure

- `src/api`: Generated API clients
- `src/components`: Reusable UI components
  - `ui`: Shadcn UI components
  - `layout`: Layout components (Sidebar, Header)
  - `features`: Feature-specific components
- `src/lib`: Utility functions and configurations (auth, api, utils)
- `src/routes`: TanStack Router route definitions
