describe('List Posts', () => {
  before(() => {
    cy.demoStudentLoginPosts();
    cy.gotoSubmitPost();
    cy.submitPost(' ', '[TEST§1] I dont understand');
  });

  afterEach(() => {
    cy.contains('Logout')
      .parent()
      .click({ force: true });
  });

  after(() => {
    cy.demoStudentLoginPosts();
    cy.gotoPosts();
    cy.deletePost('[TEST§1] I dont understand');
  });

  it('list posts as student', () => {
    cy.demoStudentLoginPosts();
    cy.gotoPosts();
  });

  it('list posts as teacher', () => {
    cy.demoTeacherLoginPosts();
    cy.gotoPosts();
  });
});
