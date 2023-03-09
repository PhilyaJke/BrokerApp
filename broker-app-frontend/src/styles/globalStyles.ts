import { createGlobalStyle } from 'styled-components';
import { darkTheme } from './themes/dark.theme';

export const GlobalStyle = createGlobalStyle`
  :root {
    font-synthesis: none;
    text-rendering: optimizeLegibility;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }

  * {
    padding: 0;
    margin: 0;
    box-sizing: border-box;
  }

  button, input, li, image {
    all: unset;
  }
  
    body {
        padding: 4px;
        max-width: 1440px;
        margin: 0 auto;
        min-height: 100vh;
        display: flex;
        background: ${darkTheme.colors.primary};
        color: ${darkTheme.colors.primaryText};
        font-family: 'Rubik', sans-serif;
        
        @media (min-width: 768px) {
        }
    }
`;