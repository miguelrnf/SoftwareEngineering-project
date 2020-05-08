describe('Suggestion list', () => {
  beforeEach(() => {
    cy.demoStudentLoginSuggestion();
  });

  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft');
    cy.contains('Logout').click();
  });

  it('login, creates a suggestion and search the suggestion\n', () => {
    cy.createSuggestion('ola', 'adeus', 'bruh');
    cy.listSuggestion('adeus');
  });

  it('login, creates a invalid suggestion and search for it', () => {
    cy.createSuggestion(' ', ' ', ' ');
    cy.get('.v-alert__dismissible > .v-btn__content > .v-icon').click();
    cy.get('[data-cy="cancel"]').click();
    cy.notfoundSuggestion('lllllalsd');
    cy.contains('No matching records found');
  });
});
