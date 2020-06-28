describe('Edit Suggestion', () => {
  before(() => {
    cy.demoStudentLoginSuggestion();
    cy.createSuggestion('TITLE', 'SUGESTAO§§§§§§§', 'OPTION');
    cy.demoTeacherLoginSuggestion();
    cy.QuickRejectSuggestion('SUGESTAO');
    cy.get('[data-cy=rejectButton]').click();
  });

  beforeEach(() => {
    cy.demoStudentLoginSuggestion();
  });

  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft');
    cy.contains('Logout').click();
  });

  it('valid edit suggestion', () => {
    cy.editSuggestion('SUGESTAO§§§§§§§', 'bababoey');
  });

  it('valid edit suggestion privacy', () => {
    cy.editSuggestionPrivacy('SUGESTAO§§§§§§§');
  });
});
