describe('Dahsboard Test', () => {
  beforeEach(() => {
    cy.demoStudentLogin();
  });

  afterEach(() => {
    cy.contains('Logout').click();
  });

  it('view dashboard', () => {
    cy.createTournament('Demo tournament', '30', true);
    cy.demoStudenDashboard();
  });

  it('view dashboard and open a tournament', () => {
    cy.listAvailableTournaments();
    cy.signInSignOut('Demo tournament');

    cy.get('.v-app-bar > .v-toolbar__content').click('bottomLeft');
    cy.wait(100);

    cy.demoStudenDashboard();
    cy.findTournamentDashboard('Demo tournament');
    cy.closeDialog();

    cy.listAvailableTournaments();
    cy.signInSignOut('Demo tournament');
    cy.listOwnTournaments();
    cy.deleteTournament('Demo tournament');
  });
});
