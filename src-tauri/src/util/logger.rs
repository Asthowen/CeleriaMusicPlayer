use env_logger::{fmt::Color, Builder, Env};
use std::io::Write;

pub fn init_logger() {
    let env: Env = Env::default();

    Builder::from_env(env)
        .format(|buf, record| {
            let mut style = buf.style();

            if record.level() == log::Level::Error {
                style.set_color(Color::Red).set_bold(true);
            } else if record.level() == log::Level::Warn {
                style.set_color(Color::Yellow).set_bold(true);
            }

            writeln!(
                buf,
                "{}",
                style.value(format!(
                    "[{}] [{}] [{}] {}",
                    record.level(),
                    record.target(),
                    chrono::Local::now().format("%d/%m/%Y - %H:%M:%S"),
                    record.args()
                )),
            )
        })
        .init();
}
