def amdahl(T, t_p, n):
    t_s = T - t_p
    return T / (t_s + t_p / n)


t_p_values = [0.25, 0.5, 0.75]
x_values = [x for x in range(0, 4)]

# Table body
for t_p in t_p_values:
    row = [f"{amdahl(1, t_p, pow(2, x)):.2f}" for x in x_values]
    print(f"{t_p} & " + " & ".join(row) + " \\\\\\hline")

        
