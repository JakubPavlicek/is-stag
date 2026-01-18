import js from '@eslint/js';
import tseslint from 'typescript-eslint';
import prettierConfig from 'eslint-config-prettier';
import globals from 'globals';

export default tseslint.config(
  {
    ignores: ['**/dist/', '**/node_modules/', '**/coverage/', 'api/'],
  },
  {
    files: ['**/*.{js,ts}'],
    extends: [js.configs.recommended, ...tseslint.configs.recommended],
    rules: {
      '@typescript-eslint/ban-ts-comment': 'off',
      '@typescript-eslint/no-explicit-any': 'off',
      '@typescript-eslint/no-unused-vars': 'warn',
    },
  },
  {
    languageOptions: {
      globals: {
        ...globals.browser,
        __ENV: 'readonly',
      },
    },
  },
  prettierConfig,
);
