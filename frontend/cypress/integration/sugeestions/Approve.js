describe('Administration walkthrough', () => {

  beforeEach(() => {
    cy.demoStudentLoginSuggestion()
    cy.createSuggestion('SUGESTAO')
    cy.demoTeacherLoginSuggestion()
  })

  afterEach(() => {
    cy.contains('Logout').click()
  })

  it('Quick Approve a Suggestion', () => {
    cy.QuickApproveSuggestion('SUGESTAO')

  });

  it('Quick Reject a Suggestion', () => {
    cy.QuickRejectSuggestion('SUGESTAO')

  });

  it('Show a Suggestion and reject', () => {
    cy.ShowSuggestion('SUGESTAO')
    cy.RejectSuggestion()
    cy.CloseSuggestion()


  });

  it('Show a Suggestion and approve', () => {
    cy.ShowSuggestion('SUGESTAO')
    cy.ApproveSuggestion()
    cy.CloseSuggestion()
  });
});
