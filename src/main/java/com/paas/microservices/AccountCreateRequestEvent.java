package com.paas.microservices;

import java.util.UUID;

public class AccountCreateRequestEvent implements Event {
	public final UUID eventId;
	public final double startingBalance;

	public AccountCreateRequestEvent(UUID eventId, double startingBalance) {
		this.eventId = eventId;
		this.startingBalance = startingBalance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(startingBalance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((eventId == null) ? 0 : eventId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AccountCreateRequestEvent other = (AccountCreateRequestEvent) obj;
		if (Double.doubleToLongBits(startingBalance) != Double.doubleToLongBits(other.startingBalance))
			return false;
		if (eventId == null) {
			if (other.eventId != null)
				return false;
		} else if (!eventId.equals(other.eventId))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "AccountCreateRequestEvent [eventId=" + eventId + ", startingBalance=" + startingBalance
				+ "]";
	}


}
