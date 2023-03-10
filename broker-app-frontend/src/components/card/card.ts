import styled from "styled-components";

export const Card = styled.div`
  background: ${props => props.theme.colors.card};
  border-radius: 16px;
  padding: 16px;
  margin: 8px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
`;
