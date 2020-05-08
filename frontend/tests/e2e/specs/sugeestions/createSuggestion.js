describe('Suggestion creation', () => {
  beforeEach(() => {
    cy.demoStudentLoginSuggestion();
  });

  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft');
    cy.contains('Logout').click();
  });

  it('login and creates a suggestion', () => {
    cy.createSuggestion('TITULO', 'O achando é esperto?', 'OPCAO');
  });

  it('login and creates a invalid suggestion', () => {
    cy.createSuggestion(' ', ' ', ' ');
    cy.get('.v-alert__dismissible > .v-btn__content > .v-icon').click();
  });
});
