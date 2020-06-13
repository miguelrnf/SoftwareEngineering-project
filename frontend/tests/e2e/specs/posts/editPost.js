describe('Edit Post', () => {
  beforeEach(() => {
    cy.demoStudentLoginPosts();
    cy.gotoSubmitPost();
    cy.submitPost(' ', '[TEST§1] I dont understand');
    cy.demoStudentLoginPosts();
  });

  afterEach(() => {
    cy.contains('Logout')
      .parent()
      .click();
  });

  it('valid edit', () => {
    cy.gotoPosts();
    cy.editPost('[TEST§1] I dont understand', '[TEST§2] I dont understand');
    cy.deletePost('[TEST§2] I dont understand');
  });

  it('invalid edit', () => {
    cy.gotoPosts();
    cy.editPost('[TEST§1] I dont understand', ' ');
    cy.closeErrorMessage()
      .get('[data-cy="cancelButton"]')
      .click();
    cy.deletePost('[TEST§1] I dont understand');
  });
});
