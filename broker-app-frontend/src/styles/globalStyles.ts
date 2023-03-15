import {createGlobalStyle} from 'styled-components';
import {darkTheme} from './themes/dark.theme';

export const GlobalStyle = createGlobalStyle`
  button, input, li, image {
    all: unset;
  }

  * {
    padding: 0;
    margin: 0;
    box-sizing: border-box;
  }


  :root {
    font-synthesis: none;
    text-rendering: optimizeLegibility;
    -webkit-font-smoothing: antialiased;
    -moz-osx-font-smoothing: grayscale;
  }


  body {
    min-height: 100vh;
    display: flex;
    background: ${darkTheme.colors.primary};
    color: ${darkTheme.colors.primaryText};
    font-family: 'Rubik', sans-serif;
  }

  main {
    max-width: 1200px;
    width: 100%;
    margin: 0 auto;
    padding: 0 6px;
    min-height: 100vh;
    scroll-behavior: smooth;
    display: block;
  }
`;