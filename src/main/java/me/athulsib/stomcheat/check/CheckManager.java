package me.athulsib.stomcheat.check;

import me.athulsib.stomcheat.check.impl.movement.speed.SpeedA;
import me.athulsib.stomcheat.check.impl.other.badpackets.BadPacketsA;
import me.athulsib.stomcheat.user.User;

import java.util.ArrayList;
import java.util.List;

public class CheckManager {

    public final List<Check> checks = new ArrayList<>();

    public void loadChecks() {

        addCheck(new SpeedA());

        addCheck(new BadPacketsA());
    }

    private void addCheck(Check check) {
        this.checks.add(check);
    }

    public void loadToPlayer(User user) {
        wrapUser(user);
        user.getChecks().addAll(this.checks);
    }

    public void wrapUser(User user) {
        this.checks.forEach(check -> check.setUser(user));
    }
}
