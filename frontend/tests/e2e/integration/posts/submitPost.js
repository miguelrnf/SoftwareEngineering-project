describe('Submit Post', () => {
  beforeEach(() => {
    cy.demoStudentLoginPosts();
  });

  afterEach(() => {
    cy.contains('Logout').click()
  });

  it('login as student and submit a post', () => {
    cy.gotoSubmitPost();

    cy.submitPost(' ', '[TEST§1] I dont understand');
    cy.deletePost('[TEST§1] I dont understand');
  });

  it('login as student and submit a post that has no student question', () => {
    cy.gotoSubmitPost();

    cy.submitPost(' ', ' ');
    cy.closeErrorMessage();
  });

  it('login as student and submit a post that has no question', () => {
    cy.gotoSubmitPost();

    cy.submitPost('§§§§', 'Wow');
    cy.closeErrorMessage();
  });

});
