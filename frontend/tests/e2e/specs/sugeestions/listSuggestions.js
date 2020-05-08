describe('Suggestion list', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft');
    cy.contains('Logout').click();
  });

  it('login, creates a suggestion and search the suggestion\n', () => {
    cy.createSuggestion('ola');
    cy.listSuggestion('ola');
  });

  it('login, creates a invalid suggestion and search for it', () => {
    cy.createSuggestion(' ');
    cy.get('.v-alert__dismissible > .v-btn__content > .v-icon').click();
    cy.get('[data-cy="cancel"]').click();
    cy.notfoundSuggestion('adeus');
    cy.contains('No matching records found');
  });
});
