describe('Comment Post', () => {
  before(() => {
    cy.demoStudentLoginPosts();
    cy.gotoSubmitPost();
    cy.submitPost(' ', '[TEST§1] I dont understand');
  });

  beforeEach(() => {
    cy.demoStudentLoginPosts();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  after(() => {
    cy.demoStudentLoginPosts();
    cy.gotoPosts();
    cy.deletePost('[TEST§1] I dont understand');
  });

  it('submit a comment', () => {
    cy.gotoPosts();
    cy.viewPost('[TEST§1] I dont understand');
    cy.comment('comment','wow');
  });

  it('submit an invalid comment', () => {
    cy.gotoPosts();
    cy.viewPost('[TEST§1] I dont understand');
    cy.comment('comment', ' ');
  });

  it('submit a reply', () => {
    cy.gotoPosts();
    cy.viewPost('[TEST§1] I dont understand');
    cy.comment('reply','amazing');
  });

});
