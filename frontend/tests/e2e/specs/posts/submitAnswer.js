describe('Post Buttons', () => {
  beforeEach(() => {
    cy.demoStudentLoginPosts();
    cy.gotoSubmitPost();
    cy.submitPost(' ', '[TEST§1] I dont understand');
  });

  beforeEach(() => {
    cy.demoTeacherLoginPosts();
  });

  afterEach(() => {
    cy.deletePost('[TEST§1] I dont understand');
    cy.contains('Logout')
      .parent()
      .click();
  });

  it('give a valid answer to a post', () => {
    cy.gotoPosts();
    cy.answerPost('[TEST§1] I dont understand', '[ANSWER§1] This is an answer');
  });

  it('give an invalid answer to a post', () => {
    cy.gotoPosts();
    cy.answerPost('[TEST§1] I dont understand', '');
  });
});
