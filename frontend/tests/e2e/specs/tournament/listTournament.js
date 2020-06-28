describe('Student walkthrough', () => {
  afterEach(() => {
    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft');
    cy.contains('Logout').click();
  });

  it('list available tournaments', () => {
    cy.demoStudentLogin();
    cy.createTournament('Demo tournament 1', '30', true, false);
    cy.createTournament('Demo tournament 2', '20', true, false);
    cy.listAvailableTournaments();
  });

  it('list enrolled tournaments', () => {
    cy.demoStudentLogin();

    cy.listEnrolledTournaments();
  });

  it('list own tournaments', () => {
    cy.demoStudentLogin();

    cy.listOwnTournaments();
  });

  it('list all tournaments', () => {
    cy.justDemoAdminLogin();
    cy.listAllTournaments();

    cy.deleteTournament('Demo tournament 1');
    cy.deleteTournament('Demo tournament 2');
  });
});
