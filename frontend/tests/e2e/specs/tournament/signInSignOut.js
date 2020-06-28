describe('Student walkthrough', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('login and signIn in tournament 1', () => {
    cy.createTournament('Demo tournament 1', '30', true, false);
    cy.wait(100);
    cy.listAvailableTournaments();

    cy.signInSignOut('Demo tournament 1');

    cy.listAvailableTournaments();
  });

  it('login and signOut in tournament 1', () => {
    cy.listAvailableTournaments();
    cy.signInSignOut('Demo tournament 1');

    cy.listOwnTournaments();
    cy.deleteTournament('Demo tournament 1');
  });
});
