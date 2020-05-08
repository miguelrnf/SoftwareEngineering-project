describe('View Post', () => {
  before(() => {
    cy.demoStudentLoginPosts();
    cy.gotoSubmitPost();

    cy.submitPost(' ', '[TEST§1] I dont understand');
  });

  afterEach(() => {
    cy.contains('Logout')
      .parent()
      .click();
  });

  after(() => {
    cy.gotoPosts();
    cy.deletePost('[TEST§1] I dont understand');
  });

  it('view dashboard', () => {
    cy.demoStudentLoginDashboard();
  });

  it('view dashboard and open a post', () => {
    cy.demoStudentLoginDashboard();
    cy.contains('[TEST§1] I dont understand').click();
  });
});
