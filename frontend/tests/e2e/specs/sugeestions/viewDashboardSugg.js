describe('View Suggestion dashboard', () => {
  before(() => {
    cy.demoStudentLoginSuggestion();
    cy.createSuggestion('TITLE', '[TEST§1] SUGGGGGGGG', 'WOW');
  });

  beforeEach(() => {
    cy.demoStudentLoginDashboard();
  });

  afterEach(() => {
    cy.contains('Logout')
      .parent()
      .click();
  });

  it('view dashboard and open a sugg', () => {
    cy.contains('[TEST§1] SUGGGGGGGG').click();
  });
});
